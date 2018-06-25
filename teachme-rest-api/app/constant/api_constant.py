IMAGE_EXTENSIONS = {'pdf', 'png', 'jpg', 'jpeg', 'gif', 'mp4', 'html'}

USERNAME_HAS_BEEN_USED = 'username.has.used'
INVALID_USERNAME = 'username.is.invalid'
PHONE_NUMBER_HAS_BEEN_USED = 'phone.number.has.used'
USER_NOT_FOUND = 'user.not.found'
FRIEND_NOT_FOUND = 'friend.not.found'
FRIEND_ALREADY_EXIST = 'friend.already.exist'
MATERIAL_NOT_FOUND = 'material.not.found'
MATERIAL_VIEWER_EXIST = 'material.viewer.already.exist'
CONNECTION_ERROR = 'connection.error'
UPLOAD_FAIL = 'upload.image.fail'
ACTIVITY_TYPE_MATERIAL = 'MATERIAL'
ACTIVITY_TYPE_TASK = 'TASK'


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() \
	       in IMAGE_EXTENSIONS
