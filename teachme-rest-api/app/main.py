from slerp.app import app
from utils.encoder import TeachmeJsonEncoder
from slerp.app import run
from api.user_principal_api import user_principal_api_blue_print
from api.health_api import health_api_blue_print
from api.school_api import school_api_blue_print
from api.document_api import document_api_blue_print
from api.material_api import material_api_blue_print
from api.material_topic_api import material_topic_api_blue_print
from api.teacher_api import teacher_api_blue_print
from api.school_class_api import school_class_api_blue_print
from api.school_level_api import school_level_api_blue_print
from api.activity_api import activity_api_blue_print
# Set Custom json encoder for Date And Byte array
app.json_encoder = TeachmeJsonEncoder

# Register API Blueprint
app.register_blueprint(user_principal_api_blue_print)
app.register_blueprint(health_api_blue_print)
app.register_blueprint(school_api_blue_print)
app.register_blueprint(document_api_blue_print)
app.register_blueprint(material_api_blue_print)
app.register_blueprint(material_topic_api_blue_print)
app.register_blueprint(teacher_api_blue_print)
app.register_blueprint(school_class_api_blue_print)
app.register_blueprint(school_level_api_blue_print)
app.register_blueprint(activity_api_blue_print)

if __name__ == '__main__':
    run()