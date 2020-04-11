node_modules:
	nvm use && npm install

_jekyll/js/main.js: node_modules
	npx shadow-cljs release app

_jekyll/index.html:
	cp src/public/index.html _jekyll/index.html

_jekyll/css/main.css: node_modules
	nvm use && npm run release-css

.PHONY: release
release: _jekyll/index.html _jekyll/js/main.js _jekyll/css/main.css
	git checkout gh-pages
	git commit -am "Release"
	git push origin HEAD
	git checkout master
	make clean

.PHONY: clean
clean:
	rm -rf _jekyll
