from sqlalchemy.orm import class_mapper


def asdict(obj):
	return dict((col.name, getattr(obj, col.name))
	            for col in class_mapper(obj.__class__).mapped_table.c)
