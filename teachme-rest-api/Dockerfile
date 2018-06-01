FROM slerp/python3
MAINTAINER Rifky aditya bastara "kiditzbastara@gmail.com"


EXPOSE 3000

WORKDIR app
RUN apk --no-cache --update-cache add poppler-utils
RUN apk --no-cache --update-cache add jpeg-dev zlib-dev lcms2-dev openjpeg-dev tiff-dev tk-dev tcl-dev
RUN apk --no-cache --update-cache add tzdata
RUN date
ENV TZ=Asia/Jakarta
COPY /requirements.txt ./requirements.txt
RUN pip3 install -r requirements.txt


COPY ./app /app
#Asia/Jakarta (WIB, +0700)
CMD [ "uwsgi", "--socket", "0.0.0.0:3000", "--processes", "2","--threads", "10", "--plugins", "python3", "--wsgi-disable-file-wrapper", "--protocol=http", "-w", "main:app" ]
