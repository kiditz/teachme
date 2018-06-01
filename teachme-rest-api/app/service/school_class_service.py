from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import SchoolClass

log = logging.getLogger(__name__)


class SchoolClassService(object):
	def __init__(self):
		super(SchoolClassService, self).__init__()
	
	@Blank(['level_id'])
	def get_school_class_by_level_id(self, domain):
		school_class_q = SchoolClass.query.filter_by(level_id=domain['level_id']).order_by(SchoolClass.id.asc()).all()
		school_class_list = list(map(lambda x: x.to_dict(), school_class_q))
		return {'payload': school_class_list}
