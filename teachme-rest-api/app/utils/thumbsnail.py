import cv2
import os

def video_to_frames(video_filename):
	"""Extract frames from video"""
	cap = cv2.VideoCapture(video_filename)
	video_length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT)) - 1
	frames = []
	if cap.isOpened() and video_length > 0:
		frame_ids = [0]
		if video_length >= 4:
			frame_ids = [0,
			             round(video_length * 0.25),
			             round(video_length * 0.5),
			             round(video_length * 0.75),
			             video_length - 1]
		count = 0
		success, image = cap.read()
		while success:
			if count in frame_ids:
				frames.append(image)
			success, image = cap.read()
			count += 1
	return frames


def video_thumbnails(path):
	frames = video_to_frames(path)
	path_name = get_filename_without_ext(path) + '.png'
	cv2.imwrite(path_name, frames[0])
	return os.path.basename(path_name)


def get_filename_without_ext(path):
	return '.'.join(path.split('.')[:-1])


if __name__ == '__main__':
	video_thumbnails('/teachme/teachme-rest-api/test/videoplayback.mp4')