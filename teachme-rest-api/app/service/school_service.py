from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Number, Blank, Key
from constant.api_constant import USER_NOT_FOUND
from entity.models import School, Address, UserPrincipal

log = logging.getLogger(__name__)


class SchoolService(object):
	def __init__(self):
		super(SchoolService, self).__init__()
	
	@Blank(['name', 'description', 'url', 'address'])
	@Number(['user_id', 'document_id'])
	def add_school(self, domain):
		self.validate_add_school(domain)
		domain['name'] = domain['name'].upper()
		address = Address({'address': domain['address']})
		address.save()
		domain.pop('address', None)
		school = School(domain)
		school.address_id = address.id
		school.active = True
		school.save()
		return {'payload': school.to_dict()}
	
	@Key(['name'])
	@Number(['page', 'size'])
	def get_school_by_name(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		
		name = domain['name']
		school_q = School.query.filter(School.name.ilike('%' + name + '%')).order_by(School.name.asc()) \
			.paginate(page, size, error_out=False)
		school_list = list(map(lambda x: x.to_dict(), school_q.items))
		return {'payload': school_list, 'total': school_q.total, 'total_pages': school_q.pages}
	
	@staticmethod
	def validate_add_school(domain):
		user_principal = UserPrincipal.query.get(domain['user_id'])
		if user_principal is None:
			raise ValidationException(USER_NOT_FOUND)
	
	@Key(['id'])
	def find_school_by_id(self, domain):
		school = School.query.filter_by(id=domain['id']).first()
		return {'payload': school.to_dict()}
