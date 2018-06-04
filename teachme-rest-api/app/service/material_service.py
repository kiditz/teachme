from slerp.logger import logging
from slerp.string_utils import is_blank
from slerp.validator import Number, Blank, Key, ValidationException

from api.teacher_api import teacher_service
from entity.models import Material, MaterialTopic

log = logging.getLogger(__name__)


class MaterialService(object):
	def __init__(self):
		super(MaterialService, self).__init__()
	
	@Blank(['title', 'description', 'type'])
	@Number(['document_id', 'user_id', 'price'])
	def add_material(self, domain):
		# Teacher variable is for validation and used to fill class id and level id
		teacher = teacher_service.find_teacher_by_user_id(domain)['payload']
		if 'topic_id' not in domain:
			topic = MaterialTopic({'user_id': teacher['user_id']})
			if is_blank(domain['name']):
				raise ValidationException('required.value.name')
			topic.name = domain['name']
			topic.class_id = teacher['class_id']
			topic.level_id = teacher['level_id']
			topic.save()
		else:
			topic = MaterialTopic.query.get(domain['topic_id'])
			domain.pop('topic_id')
		pass
		
		material = Material(domain)
		material.topic_id = topic.id
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
		material_q = Material.query.filter_by(topic_id=domain['topic_id']).order_by(Material.id.desc()) \
			.paginate(page, size, error_out=False)
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
