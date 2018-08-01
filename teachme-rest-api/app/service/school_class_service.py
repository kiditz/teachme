from slerp.logger import logging
from slerp.validator import Blank, Number

from entity.models import SchoolClass

log = logging.getLogger(__name__)


class SchoolClassService(object):
	def __init__(self):
		super(SchoolClassService, self).__init__()
		
	@Number(['level_id'])
	@Blank(['name'])
	def add_school_class(self, domain):
		school_class = SchoolClass(domain)
		school_class.save()
		return {'payload': school_class.to_dict()}
	
	@Number(['level_id'])
	def get_school_class_by_level_id(self, domain):
		school_class_q = SchoolClass.query.filter_by(level_id=domain['level_id']).order_by(SchoolClass.id.asc()).all()
		school_class_list = list(map(lambda x: x.to_dict(), school_class_q))
		return {'payload': school_class_list}