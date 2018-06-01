from slerp.logger import logging
from slerp.validator import Blank

from entity.models import Student, UserPrincipal

log = logging.getLogger(__name__)


class StudentService(object):
	def __init__(self):
		super(StudentService, self).__init__()
	
	@Blank(['username'])
	def find_student_by_user_id(self, domain):
		student = Student.query.join(UserPrincipal).filter(UserPrincipal.username == domain['username']).first()
		return {'payload': student.to_dict()}
