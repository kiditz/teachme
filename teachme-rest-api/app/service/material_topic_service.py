from slerp.logger import logging
from slerp.validator import Number, Blank

from entity.models import MaterialTopic

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
	
	@Number(['page', 'size'])
	def get_material_topic(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		material_topic_q = MaterialTopic.query.order_by(MaterialTopic.name.asc()).paginate(page, size, error_out=False)
		material_topic_list = list(map(lambda x: x.to_dict(), material_topic_q.items))
		return {'payload': material_topic_list, 'total': material_topic_q.total, 'total_pages': material_topic_q.pages}
	
	@Blank(['level_id'])
	@Number(['page', 'size'])
	def get_material_topic_by_level_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		material_topic_q = MaterialTopic.query.filter_by(level_id=domain['level_id']).order_by(MaterialTopic.name.desc()).paginate(page, size, error_out=False)
		material_topic_list = list(map(lambda x: x.to_dict(), material_topic_q.items))
		return {'payload': material_topic_list, 'total': material_topic_q.total, 'total_pages': material_topic_q.pages}