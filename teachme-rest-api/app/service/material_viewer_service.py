from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Number

from constant.api_constant import MATERIAL_VIEWER_EXIST, MATERIAL_NOT_FOUND, MATERIAL_IS_NOT_ACTIVE, MATERIAL_VIEWER_MUST_NOT_CREATOR
from entity.models import MaterialViewer, Material

log = logging.getLogger(__name__)


class MaterialViewerService(object):
	def __init__(self):
		super(MaterialViewerService, self).__init__()

	@Number(['user_id', 'material_id'])
	def add_material_viewer(self, domain):
		material = Material.query.get(domain['material_id'])
		if material is None:
			raise ValidationException(MATERIAL_NOT_FOUND)
		if material.active != 'A':
			raise ValidationException(MATERIAL_IS_NOT_ACTIVE)
		viewer = MaterialViewer.query.filter_by(user_id=domain['user_id']).filter_by(material_id=domain['material_id']).first()
		if viewer is not None:
			raise ValidationException(MATERIAL_VIEWER_EXIST)
		if material.user_id == domain['user_id']:
			raise ValidationException(MATERIAL_VIEWER_MUST_NOT_CREATOR)
		material_viewer = MaterialViewer(domain)
		material_viewer.save()
		return {'payload': material_viewer.to_dict()}
		
	@Number(['material_id'])
	def count_material_viewer_by_material_id(self, domain):
		count_material_viewer = MaterialViewer.query.filter_by(material_id=domain['material_id']).count()
		return {'payload': {'count': count_material_viewer}}