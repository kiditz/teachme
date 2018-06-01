from slerp.logger import logging
from slerp.validator import Number, Blank, Key

from entity.models import Material

log = logging.getLogger(__name__)


class MaterialService(object):
	def __init__(self):
		super(MaterialService, self).__init__()
	
	@Blank(['title', 'description', 'type'])
	@Number(['document_id', 'user_id', 'price', "topic_id"])
	def add_material(self, domain):
		material = Material(domain)
		material.save()
		return {'payload': material.to_dict()}
	
	@Key(['title'])
	@Number(['page', 'size'])
	def get_material_by_title(self, domain):
		title = domain['title']
		page = int(domain['page'])
		size = int(domain['size'])
		material_q = Material.query.filter(Material.title.ilike('%' + title + '%')).order_by(
			Material.id.desc()).paginate(page, size, error_out=False)
		
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
	
	@Number(['page', 'size', 'topic_id'])
	def get_material_by_topic_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		material_q = Material.query.filter_by(topic_id=domain['topic_id']).order_by(Material.id.desc())\
			.paginate(page, size, error_out=False)
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
