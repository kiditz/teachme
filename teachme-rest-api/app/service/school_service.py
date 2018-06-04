from slerp.logger import logging
from slerp.validator import Number, Blank, Key

from api.user_principal_api import user_principal_service
from entity.models import School, Address

log = logging.getLogger(__name__)


class SchoolService(object):
	def __init__(self):
		super(SchoolService, self).__init__()
	
	@Blank(['name', 'description', 'url', 'address'])
	@Number(['user_id', 'document_id'])
	def add_school(self, domain):
		user_principal_service.find_user_principal_by_id(domain)
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
		
	@Key(['id'])
	def find_school_by_id(self, domain):
		school = School.query.filter_by(id=domain['id']).first()
		return {'payload': school.to_dict()}
