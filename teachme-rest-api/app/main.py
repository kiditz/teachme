from slerp.app import app
from utils.encoder import TeachmeJsonEncoder
from slerp.app import run
from api.user_principal_api import user_principal_api_blue_print
from api.health_api import health_api_blue_print
from api.document_api import document_api_blue_print
from api.lesson_api import lesson_api_blue_print
from api.topic_api import topic_api_blue_print
from api.school_class_api import school_class_api_blue_print
from api.school_level_api import school_level_api_blue_print
from api.activity_api import activity_api_blue_print
from api.friend_api import friend_api_blue_print
from api.lesson_viewer_api import lesson_viewer_api_blue_print
from api.lesson_comment_api import lesson_comment_api_blue_print
# Set Custom json encoder for Date And Byte array
app.json_encoder = TeachmeJsonEncoder

# Register API Blueprint
app.register_blueprint(user_principal_api_blue_print)
app.register_blueprint(health_api_blue_print)
app.register_blueprint(document_api_blue_print)
app.register_blueprint(lesson_api_blue_print)
app.register_blueprint(topic_api_blue_print)
app.register_blueprint(school_class_api_blue_print)
app.register_blueprint(school_level_api_blue_print)
app.register_blueprint(activity_api_blue_print)
app.register_blueprint(friend_api_blue_print)
app.register_blueprint(lesson_viewer_api_blue_print)
app.register_blueprint(lesson_comment_api_blue_print)

if __name__ == '__main__':
    run()