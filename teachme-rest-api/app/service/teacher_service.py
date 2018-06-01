from slerp.logger import logging
from slerp.validator import Blank, ValidationException

from constant.api_constant import USER_NOT_FOUND
from entity.models import Teacher
from entity.models import UserPrincipal

log = logging.getLogger(__name__)


class TeacherService(object):
	def __init__(self):
		super(TeacherService, self).__init__()
	
	@Blank(['username'])
	def find_teacher_by_username(self, domain):
		teacher = Teacher.query.join(UserPrincipal).filter(UserPrincipal.username == domain['username']).first()
		if teacher is None:
			raise ValidationException(USER_NOT_FOUND)
		return {'payload': teacher.to_dict()}
