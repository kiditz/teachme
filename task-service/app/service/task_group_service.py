from slerp.validator import Key, Number, Blank, ValidationException
from slerp.logger import logging
from slerp.app import db
from constant.api_constant import ErrorCode
from entity.models import TaskGroup, Task


log = logging.getLogger(__name__)


class TaskGroupService(object):
	def __init__(self):
		super(TaskGroupService, self).__init__()

	@Key(['task_id'])
	def add_task_group(self, domain):
		task = Task.query.get(domain['task_id'])
		if task is None:
			raise ValidationException(ErrorCode.TASK_NOT_FOUND)
		if 'groups' not in domain:
			raise ValidationException(ErrorCode.GROUP_NOT_FOUND)
		if len(domain['groups']) == 0:
			raise ValidationException(ErrorCode.GROUP_CANT_EMPTY)
		
		task.active = True
		task.save()		
		groups = domain['groups']
		for index, group in enumerate(groups):
			group['task_id'] = task.id
			groups[index] = group
		log.info(groups)
		db.session.bulk_insert_mappings(TaskGroup, groups)
		db.session.flush()		
		return {'payload': task.to_dict()}