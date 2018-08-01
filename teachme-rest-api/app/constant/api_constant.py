IMAGE_EXTENSIONS = {'pdf', 'png', 'jpg', 'jpeg', 'gif', 'mp4', 'html'}


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() \
	       in IMAGE_EXTENSIONS


class ActivityMessage(object):
	NEW_LESSON = 'create.new.lesson'
	START_FOLLOW = 'start.following'
	READ_LESSON = 'read.lesson'
	

class ActivityType(object):
	LESSON = 'LESSON'
	LESSON_VIEWER = 'LESSON_VIEWER'
	FOLLOW = 'FOLLOW'
	TASK = 'TASK'
	

class ErrorCode(object):
	USERNAME_HAS_BEEN_USED = 'username.has.used'
	INVALID_USERNAME = 'username.is.invalid'
	PHONE_NUMBER_HAS_BEEN_USED = 'phone.number.has.used'
	USER_NOT_FOUND = 'user.not.found'
	FRIEND_NOT_FOUND = 'friend.not.found'
	FRIEND_ALREADY_EXIST = 'friend.already.exist'
	LESSON_NOT_FOUND = 'lesson.not.found'
	LESSON_IS_NOT_ACTIVE = 'lesson.is.not.active'
	LESSON_VIEWER_MUST_NOT_CREATOR = 'lesson.viewer.must.not.creator'
	ONLY_IN_PROGRESS_CAN_BE_ACTIVATED = 'only.in.progress.can.be.activated'
	LESSON_VIEWER_EXIST = 'lesson.viewer.already.exist'
	CONNECTION_ERROR = 'connection.error'
	UPLOAD_FAIL = 'upload.image.fail'