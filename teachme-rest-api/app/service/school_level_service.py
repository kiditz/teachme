from slerp.logger import logging

from entity.models import SchoolLevel

log = logging.getLogger(__name__)


class SchoolLevelService(object):
	def __init__(self):
		super(SchoolLevelService, self).__init__()
	
	def get_school_level(self, domain):
		school_level_q = SchoolLevel.query.order_by(SchoolLevel.sort_number.asc()).all()
		school_level_list = list(map(lambda x: x.to_dict(), school_level_q))
		return {'payload': school_level_list}
