FROM node:13
WORKDIR /app
COPY node_modules node_modules
COPY index.js index.js
COPY package.json package.json
COPY package-lock.json package-lock.json
RUN npm install -g nodemon
#RUN npm start
CMD node index.js
EXPOSE 5000