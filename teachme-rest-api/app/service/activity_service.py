from slerp.logger import logging
from slerp.validator import Key, Number

from entity.models import Activity, Friend

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
		activity_q = Activity.query.filter_by(user_id=domain['user_id']).order_by(Activity.id.desc()).paginate(page, size, error_out=False)
		activity_list = list(map(lambda x: x.to_dict(), activity_q.items))
		return {'payload': activity_list, 'total': activity_q.total, 'total_pages': activity_q.pages}
	
	@Number(['page', 'size', 'user_id'])
	def get_all_activity(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		# Get activity from friend
		activity_q1 = Activity.query.join(Friend, Friend.friend_id == Activity.user_id)\
			.filter(Friend.user_id == domain['user_id'])
		# Get activity from user
		activity_q2 = Activity.query.filter_by(user_id=domain['user_id'])
		# Merging activity by using union
		query = activity_q1.union(activity_q2).order_by(Activity.id.desc()).paginate(page, size, error_out=False)
		activity_list = list(map(lambda x: x.to_dict(), query.items))
		return {'payload': activity_list, 'total': query.total, 'total_pages': query.pages}