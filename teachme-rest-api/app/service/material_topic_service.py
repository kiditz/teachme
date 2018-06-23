from slerp.logger import logging
from slerp.validator import Number, Blank, Key

from entity.models import MaterialTopic, Friend

log = logging.getLogger(__name__)


class MaterialTopicService(object):
	def __init__(self):
		super(MaterialTopicService, self).__init__()

	@Number(['user_id'])
	@Blank(['name'])
	def add_material_topic(self, domain):
		material_topic = MaterialTopic(domain)
		material_topic.save()
		return {'payload': material_topic.to_dict()}
		
	@Key(['name'])
	@Number(['page', 'size', 'level_id', 'user_id'])
	def get_material_topic(self, domain):
		
		page = int(domain['page'])
		size = int(domain['size'])
		material_topic_q1 = MaterialTopic.query.join(Friend, Friend.friend_id == MaterialTopic.user_id) \
			.filter(Friend.user_id == domain['user_id'])
		material_topic_q2 = MaterialTopic.query.filter(MaterialTopic.name.ilike('%' + domain['name'] + '%'))\
			.filter(MaterialTopic.level_id == domain['level_id'])
		material_topic_q = material_topic_q1.union(material_topic_q2).order_by(MaterialTopic.name.asc()).paginate(page, size, error_out=False)
		material_topic_list = list(map(lambda x: x.to_dict(), material_topic_q.items))
		return {'payload': material_topic_list, 'total': material_topic_q.total, 'total_pages': material_topic_q.pages}