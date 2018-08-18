from slerp.app import app, run
from utils import TeachmeJsonEncoder
from api.task_api import task_api_blue_print
from api.task_group_api import task_group_api_blue_print
from api.health_api import health_api_blue_print
from messaging import TaskScoreConsumer
import sys
import time
from slerp.logger import logging

log = logging.getLogger(__name__)
app.json_encoder = TeachmeJsonEncoder
app.register_blueprint(task_api_blue_print)
app.register_blueprint(task_group_api_blue_print)
app.register_blueprint(health_api_blue_print)
tasks = []


@app.before_first_request
@app.route('/start')
def start():
    log.info("Start")
    if len(tasks) == 0:
        tasks.append(TaskScoreConsumer())
    for t in tasks:
        if not t.is_alive():
            t.start()
            return {'payload': True}
        else:
            return {'payload': 'Has been running before'}
    

@app.route('/stop')
def stop():
    log.info("Stop consumer")
    if len(tasks) == 0:
        return {'payload': 'Not running'}
    for t in tasks:
        if t is not None:
            t.stop()
            t.terminate()
    tasks.clear()
    return {'payload': True}


if __name__ == '__main__':
    try:
        run()
    except KeyboardInterrupt:
        print("Exit")
        time.sleep(10)
        for task in tasks:
            task.stop()
            task.join()
        sys.exit(0)
