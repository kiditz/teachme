import re
from slerp.logger import logging
from slerp.validator import Key, Blank, Number
from entity.models import Task, TaskQuestion, TaskAnswer
log = logging.getLogger(__name__)


class TaskService(object):
	def __init__(self):
		super(TaskService, self).__init__()
	
	@Key(['title', 'user_id'])
	def add_task(self, domain):
		if 'active' not in domain:
			domain['active'] = False
		task_data = {
		  'title': domain['title'],
		  'active': domain['active'],
		  'user_id': domain['user_id']		  
		}
		if 'time_limit' in domain:
			task_data['time_limit'] = re.sub('[^0-9]', '', str(domain['time_limit']).lower())
			
		task = Task(task_data)
		task.save()
		questions = domain['questions']
		for question in questions:
			question_data = {
				'question': question['question'],
				'question_type': question['type']				
			}
			if 'answer_key' in question:
				question_data['answer_key'] = question['answer_key']
			question_data['task_id'] = task.id
			task_question = TaskQuestion(question_data)
			task_question.save()
			if 'answers' in question:
				answers = question['answers']
				for answer in answers:
					answer_data = {
						'answer': answer,
						'question_id': task_question.id
					}					
					task_answer = TaskAnswer(answer_data)
					task_answer.save()
					pass
			pass
		# task.save()
		return {'payload': task.to_dict()}

	@Blank(['user_id', 'active'])
	@Number(['page', 'size'])
	def get_task_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		task_q = Task.query.filter_by(user_id=domain['user_id'], active=domain['active']).order_by(Task.id.desc()).paginate(page, size, error_out=False)
		task_list = list(map(lambda x: x.to_dict(), task_q.items))
		return {'payload': task_list, 'total': task_q.total, 'total_pages': task_q.pages}