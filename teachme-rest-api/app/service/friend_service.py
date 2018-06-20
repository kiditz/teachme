from slerp.logger import logging
from slerp.validator import Key, Number

from entity.models import Friend

log = logging.getLogger(__name__)


class FriendService(object):
	def __init__(self):
		super(FriendService, self).__init__()
	
	@Key(['user_id', 'friend_id', 'status'])
	def add_friend(self, domain):
		friend = Friend(domain)
		friend.save()
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
