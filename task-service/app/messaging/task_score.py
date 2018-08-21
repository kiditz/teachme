import multiprocessing
import json
import time
from kafka import KafkaConsumer, TopicPartition
from slerp.app import app, db
from slerp.logger import logging
from entity.models import TaskQuestion, TaskScore, QuestionScore
from sqlalchemy import exc
log = logging.getLogger(__name__)


class TaskScoreConsumer(multiprocessing.Process):

    def __init__(self):
        multiprocessing.Process.__init__(self)
        self.consumer = KafkaConsumer(
            group_id='task-group', bootstrap_servers=[app.config['KAFKA_SERVER']])
        self.stop_event = multiprocessing.Event()

    def stop(self):
        self.stop_event.set()

    def run(self):
        self.consumer.assign([TopicPartition('task', 0)])
        while not self.stop_event.is_set():
            log.info('Stop Event : %s', self.stop_event.is_set())
            for msg in self.consumer:
                if self.stop_event.is_set():
                    break
                log.info('partition %s', msg.partition)
                log.info('partition %s', msg.offset)
                task = json.loads(msg.value.decode('utf-8'))
                log.info('task : %s', task)
                answers = task['answers']
                answers_list = []
                for it in answers:
                    question = TaskQuestion.query.get(it['question_id'])
                    total_mc = TaskQuestion.query.filter_by(
                        id=it['question_id']).filter_by(question_type='MC').count()
                    total_non_mc = TaskQuestion.query.filter_by(id=it['question_id']).filter(
                        TaskQuestion.question_type != 'MC').count()
                    total_mc = float(total_mc)
                    score_mc = 0.0
                    user_answer = it['answer']
                    if question.question_type == 'MC':
                        answer_key = question.answer_key                        
                        answer_input = {
                            'question_id': it['question_id'],
                            'task_id': task['task_id']
                            'user_id': task['user_id'],
                            'user_answer': user_answer
                        }
                        if user_answer == answer_key:
                            answer_input['score'] = 1.0
                            score_mc += 1.0
                        else:
                            answer_input['score'] = 0.0
                    else:
                        answer_input = {
                            'question_id': it['question_id'],
                            'user_id': task['user_id'],
                            'user_answer': user_answer
                        }                        
                        answer_input['score'] = 0.0
                    answers_list.append(answer_input)
                    log.info('answers_list : %s', answers_list)
                    log.info('total_mc : %s', total_mc)
                    log.info('total_score_mc : %s', score_mc)
                    #total_score = (score_mc / total_mc) * 100
                    #log.info('task_score: %s', total_score)

                try:
                    db.session.bulk_insert_mappings(
                        QuestionScore, answers_list)
                    db.session.commit()
                except exc.IntegrityError:
                    log.info("Data has been saved before")
                    db.session.rollback()

            self.consumer.close()
