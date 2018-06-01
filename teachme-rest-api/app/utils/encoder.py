import calendar
from datetime import datetime
from decimal import Decimal
from flask.json import JSONEncoder


class TeachmeJsonEncoder(JSONEncoder):
	
	def default(self, obj):
		try:
			if isinstance(obj, (bytes, bytearray)):
				return obj.decode("ASCII")
			elif isinstance(obj, Decimal):
				return float(obj)
			elif isinstance(obj, datetime):
				if obj.utcoffset() is not None:
					obj = obj - obj.utcoffset()
				millis = int(
					calendar.timegm(obj.timetuple()) * 1000 +
					obj.microsecond / 1000
				)
				return millis
		except TypeError:
			raise
		return JSONEncoder.default(self, obj)
