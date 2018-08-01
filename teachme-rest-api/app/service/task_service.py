from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import Task


log = logging.getLogger(__name__)


class TaskService(object):
	def __init__(self):
		super(TaskService, self).__init__()

	@Key(['id', 'title', 'user_id', 'active'])
	def add_task(self, domain):
		task = Task(domain)
		task.save()
		return {'payload': task.to_dict()}