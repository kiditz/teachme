import subprocess
import os
import fnmatch
from slerp.logger import logging
from PIL import Image
from PyPDF2 import PdfFileReader
log = logging.getLogger(__name__)


def save_pdf_image(input=None, output=None):
	if not output.endswith('/'):
		output += '/'
	if not os.path.isdir(output):
		os.makedirs(output)
	filename = get_file_name_without_ext(os.path.basename(input))
	out_file = output + filename
	# out_temp = out_file
	command = 'pdftoppm -png -l 1 {} {}'.format(input, out_file)
	log.info('Command >>> %s', command)
	subprocess.call(command, shell=True, timeout=100)
	glob_path = recursive_glob(output, filename + '-*.png')[0]
	# Resizing image result
	img = Image.open(glob_path)
	half = 0.5
	img = img.resize([int(half * s) for s in img.size])
	img.save(glob_path)
	# End resizing
	return glob_path
	

def get_file_name_without_ext(filename):
	return filename.rsplit('.', 1)[0]


def get_page_num(file_path):
	file = open(file_path, 'r+b')		
	try:
		size = os.fstat(file.fileno()).st_size
		log.info('file size >>> %s', size)
		pdf = PdfFileReader(file, strict=False)
		return int(pdf.getNumPages())
	except Exception as e:
		raise e
	finally:
		file.close()


def recursive_glob(rootdir='.', pattern='*'):
	return [os.path.join(looproot, filename)
            for looproot, _, filenames in os.walk(rootdir)
            for filename in filenames
            if fnmatch.fnmatch(filename, pattern)]


if __name__ == '__main__':
	print(recursive_glob('/home/kiditz/2018-02-26/', '2018_02_26_01_31_1519608710-*.png'))