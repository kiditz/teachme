FROM slerp/python3
MAINTAINER Rifky aditya bastara "kiditzbastara@gmail.com"


EXPOSE 3000

WORKDIR app
RUN apk --no-cache --update-cache add poppler-utils
RUN apk --no-cache --update-cache add libffi libffi-dev jpeg-dev zlib-dev lcms2-dev openjpeg-dev tiff-dev tk-dev tcl-dev
RUN apk --no-cache --update-cache add tzdata
ENV TZ=Asia/Jakarta
RUN date

COPY /requirements.txt ./requirements.txt
COPY ./app /app
RUN pip3 install --no-cache-dir --upgrade pip -r requirements.txt

CMD [ "uwsgi", "--socket", "0.0.0.0:5002", "--processes", "2","--threads", "10", "--plugins", "python3", "--wsgi-disable-file-wrapper", "--protocol=http", "-w", "main:app" ]
