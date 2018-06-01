IMAGE_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif', 'mp4'])

USERNAME_HAS_BEEN_USED = 'username.has.used'
INVALID_USERNAME = 'username.is.invalid'
PHONE_NUMBER_HAS_BEEN_USED = 'phone.number.has.used'
USER_NOT_FOUND = 'user.not.found'
CONNECTION_ERROR = 'connection.error'
UPLOAD_FAIL = 'upload.image.fail'


def allowed_image(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() \
	       in IMAGE_EXTENSIONS
