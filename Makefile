node_modules:
	npm install

js/main.js: node_modules
	./node_modules/.bin/shadow-cljs release app

index.html:
	mkdir -p _jekyll
	cp src/public/index.html index.html

css/main.css: node_modules
	npm run release-css

.PHONY: release
release: index.html js/main.js css/main.css
	git checkout -m gh-pages
	git add .
	git commit -m "Release"
	git push origin HEAD
	git checkout master
	make clean

.PHONY: clean
clean:
	rm -rf css js index.html
