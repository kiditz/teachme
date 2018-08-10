from slerp.logger import logging
from slerp.validator import Key, Number
from sqlalchemy import func, case

from flask import json
from constant.api_constant import ActivityType
from entity.models import Activity, Friend, UserPrincipal, SchoolClass, SchoolLevel

log = logging.getLogger(__name__)


class ActivityService(object):
	def __init__(self):
		super(ActivityService, self).__init__()
	
	@Key(['message', 'user_id'])
	def add_activity(self, domain):
		activity = Activity(domain)
		activity.save()
		return {'payload': activity.to_dict()}
	
	@Number(['page', 'size', 'user_id'])
	def get_activity_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		activity_q = Activity.query.filter_by(user_id=domain['user_id']).order_by(Activity.id.desc()).paginate(page,
		                                                                                                       size,
		                                                                                                       error_out=False)
		activity_list = list(map(lambda x: x.to_dict(), activity_q.items))
		return {'payload': activity_list, 'total': activity_q.total, 'total_pages': activity_q.pages}
	
	@Number(['page', 'size', 'user_id'])
	def get_all_activity(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		# Get activity from friend
		entities = (
			Activity.id,
			Activity.doc_type,
			Activity.message,
			Activity.created_at,
			Activity.user_id,
			Activity.raw,
			UserPrincipal.username,
			UserPrincipal.fullname
		)
		
		activity_q1 = Activity.query.with_entities(*entities) \
			.join(Friend, Friend.friend_id == Activity.user_id) \
			.join(UserPrincipal, UserPrincipal.id == Activity.user_id) \
			.filter(Friend.user_id == domain['user_id'])
		
		# Get activity from user
		activity_q2 = Activity.query.with_entities(*entities) \
			.join(UserPrincipal, UserPrincipal.id == Activity.user_id) \
			.filter(Activity.user_id == domain['user_id'])
		# Merging activity by using union
		query = activity_q1.union(activity_q2).order_by(Activity.created_at.desc()).paginate(page, size, error_out=False)
		activity_list = list(map(lambda x: handle_activity_item(x._asdict()), query.items))
		return {'payload': activity_list, 'total': query.total, 'total_pages': query.pages}


def handle_activity_item(activity):
	raw = json.loads(str(activity['raw']).encode('utf-8'))
	if activity['doc_type'] == ActivityType.FOLLOW:
		# Translate register_type to indonesian language
		register_type = case([
			(UserPrincipal.register_type == 'STUDENT', 'Siswa')
		], else_='Guru').label('type')
		
		# Process query using upper concat and case expr
		user_dict = UserPrincipal.query.with_entities(
			UserPrincipal.fullname.label('title'),
			UserPrincipal.document_id,
			func.upper(
				func.concat(
					register_type, ' ',
					func.coalesce(SchoolClass.name, ''), ' ',
					SchoolLevel.name
				)
			).label('detail')
		).outerjoin(SchoolClass, SchoolClass.id == UserPrincipal.class_id) \
			.join(SchoolLevel, SchoolLevel.id == UserPrincipal.level_id) \
			.filter(UserPrincipal.id == raw['friend_id']).first()._asdict()
		activity['detail'] = user_dict
		pass
	activity.pop('raw')
	return activity
