IMAGE_EXTENSIONS = set(['pdf', 'png', 'jpg', 'jpeg', 'gif', 'mp4', 'html'])

USERNAME_HAS_BEEN_USED = 'username.has.used'
INVALID_USERNAME = 'username.is.invalid'
PHONE_NUMBER_HAS_BEEN_USED = 'phone.number.has.used'
USER_NOT_FOUND = 'user.not.found'
MATERIAL_NOT_FOUND = 'material.not.found'
CONNECTION_ERROR = 'connection.error'
UPLOAD_FAIL = 'upload.image.fail'


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() \
	       in IMAGE_EXTENSIONS
