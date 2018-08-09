import json
from slerp.exception import ValidationException
from slerp.logger import logging
from sqlalchemy import or_
from slerp.validator import Key, Number
from utils.encoder import TeachmeJsonEncoder
from constant.api_constant import ActivityMessage, ActivityType, ErrorCode
from entity.models import Friend, UserPrincipal, Activity, SchoolClass, SchoolLevel
from api.activity_api import activity_service

log = logging.getLogger(__name__)


class FriendService(object):
	def __init__(self):
		super(FriendService, self).__init__()
	
	@Key(['user_id', 'friend_id', 'status'])
	def add_friend(self, domain):
		# Validate user_id should be exists
		user_principal = UserPrincipal.query.filter_by(id=domain['user_id']).first()
		if user_principal is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		# Validate friend_id should be exists
		user_principal = UserPrincipal.query.filter_by(id=domain['friend_id']).first()
		if user_principal is None:
			raise ValidationException(ErrorCode.FRIEND_NOT_FOUND)
		# Validate friend exists
		friend_count = Friend.query.filter_by(user_id=domain['user_id'], friend_id=domain['friend_id']).count()
		if friend_count:
			raise ValidationException(ErrorCode.FRIEND_ALREADY_EXIST)
		
		friend = Friend(domain)
		friend.save()
		friend_dict = friend.to_dict()
		activity_domain = {'user_id': friend.user_id,
		                   'message': ActivityMessage.START_FOLLOW,
		                   'raw': json.dumps(friend_dict, indent=4, sort_keys=True, cls=TeachmeJsonEncoder),
		                   'doc_type': ActivityType.FOLLOW}
		activity_service.add_activity(activity_domain)
		return {'payload': friend.to_dict()}
	
	@Number(['user_id', 'page', 'size'])
	def get_friend_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		friend_q = Friend.query.filter_by(user_id=domain['user_id']).order_by(Friend.created_at.desc()).paginate(page,
		                                                                                                         size,
		                                                                                                         error_out=False)
		friend_list = list(map(lambda x: x.to_dict(), friend_q.items))
		return {'payload': friend_list, 'total': friend_q.total, 'total_pages': friend_q.pages}
	
	@Key(['user_id', 'friend_id'])
	def delete_friend(self, domain):
		friend = Friend.query.filter_by(user_id=domain['user_id'], friend_id=domain['friend_id']).first()
		friend.delete()
		return {'payload': {'success': True}}
	
	@Key(['user_id'])
	def count_following_followers(self, domain):
		user_principal = UserPrincipal.query.filter_by(id=domain['user_id']).first()
		if user_principal is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		following = Friend.query.filter_by(user_id=domain['user_id']).count()
		follower = Friend.query.filter_by(friend_id=domain['user_id']).count()
		activity = Activity.query.filter(Activity.user_id == domain['user_id']).count()
		return {'payload': {'total_following': following, 'total_follower': follower, 'total_activity': activity}}
	
	@Key(['query'])
	@Number(['page', 'size', 'user_id'])
	def get_friends(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		user_id = domain['user_id']
		query = domain['query']
		user_principal_q = UserPrincipal.query \
			.with_entities(UserPrincipal.id, UserPrincipal.username, UserPrincipal.fullname,
		                   SchoolLevel.name.label('level'),
		                   SchoolClass.name.label('class_name'),
		                   UserPrincipal.register_type) \
			.join(SchoolLevel, UserPrincipal.level_id == SchoolLevel.id) \
			.outerjoin(SchoolClass, UserPrincipal.class_id == SchoolClass.id) \
			.filter(UserPrincipal.id != user_id) \
			.filter(or_(UserPrincipal.username.ilike('%' + query + '%'), UserPrincipal.fullname.ilike('%' + query + '%'))) \
			.paginate(page, size, error_out=False)
		users = []
		for user in user_principal_q.items:
			user_dict = user._asdict()
			count_follower = Friend.query.filter_by(user_id=user_dict['id'], friend_id=user_id).count()
			count_following = Friend.query.filter_by(user_id=user_id, friend_id=user_dict['id']).count()
			user_dict['is_following'] = (count_following > 0)
			user_dict['is_follower'] = (count_follower > 0)
			users.append(user_dict)
		return {'payload': users, 'total': user_principal_q.total, 'total_pages': user_principal_q.pages}
