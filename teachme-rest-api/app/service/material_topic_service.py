from slerp.logger import logging
from slerp.validator import Number, Blank, Key

from entity.models import Topic, Lesson, Friend

log = logging.getLogger(__name__)


class MaterialTopicService(object):
	def __init__(self):
		super(MaterialTopicService, self).__init__()

	@Number(['user_id'])
	@Blank(['name'])
	def add_lesson_topic(self, domain):
		material_topic = Topic(domain)
		material_topic.save()
		return {'payload': material_topic.to_dict()}
		
	@Key(['name'])
	@Number(['page', 'size', 'user_id'])
	def get_lesson_topic(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		# Get material topic by friend and active and by it's name
		material_topic_q1 = Topic.query\
			.join(Friend, Friend.friend_id == Topic.user_id) \
			.join(Lesson, Topic.id == Lesson.topic_id)\
			.filter(Lesson.active == 'A') \
			.filter(Topic.name.ilike('%' + domain['name'] + '%')) \
			.filter(Friend.user_id == domain['user_id'])
		# Get material topic by user and name
		if 'level_id' in domain:
			material_topic_q2 = Topic.query\
				.join(Lesson, Topic.id == Lesson.topic_id)\
				.filter(Lesson.active == 'A')\
				.filter(Topic.name.ilike('%' + domain['name'] + '%'))\
				.filter(Topic.level_id == domain['level_id'])
			material_topic_q = material_topic_q1.union(material_topic_q2).order_by(Topic.name.asc()).paginate(page,
			                                                                                                  size,
			                                                                                                  error_out=False)
			material_topic_list = list(map(lambda x: x.to_dict(), material_topic_q.items))
			return {'payload': material_topic_list, 'total': material_topic_q.total,
			        'total_pages': material_topic_q.pages}
		else:
			material_topic_q1.paginate(page, size, error_out=False)
			material_topic_list = list(map(lambda x: x.to_dict(), material_topic_q1.items))
			return {'payload': material_topic_list, 'total': material_topic_q1.total,
			        'total_pages': material_topic_q1.pages}