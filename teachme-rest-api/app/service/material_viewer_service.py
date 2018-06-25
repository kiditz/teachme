from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Number

from constant.api_constant import MATERIAL_VIEWER_EXIST
from entity.models import MaterialViewer

log = logging.getLogger(__name__)


class MaterialViewerService(object):
	def __init__(self):
		super(MaterialViewerService, self).__init__()

	@Number(['user_id', 'material_id'])
	def add_material_viewer(self, domain):
		viewer = MaterialViewer.query.filter_by(user_id=domain['user_id']).filter_by(material_id=domain['material_id'])
		if viewer is not None:
			raise ValidationException(MATERIAL_VIEWER_EXIST)
		material_viewer = MaterialViewer(domain)
		material_viewer.save()
		return {'payload': material_viewer.to_dict()}