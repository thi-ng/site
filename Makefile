BUILD = build
RES = resources/public
SRC = src/thi/ng/site

MAIN = $(BUILD)/main
MAIN_TARGET = s3://thi.ng/
MAIN_SRC = $(SRC)/main

WS = $(BUILD)/workshop
WS_TARGET = s3://workshop.thi.ng/
WS_SRC = $(SRC)/workshop

HTML_OPTS = --remove-surrounding-spaces max

main-html: $(MAIN)/index.html
main-js: $(MAIN)/js/main/app.js
main-css: $(MAIN)/css/style.css
main-fonts: $(MAIN)/fonts
main-img: $(MAIN)/img

ws-html: $(WS)/index.html
ws-js: $(WS)/js/workshop/app.js
ws-css: $(WS)/css/workshop.css
ws-fonts: $(WS)/fonts
ws-img: $(WS)/img

############# Main

$(MAIN)/index.html: $(RES)/index.html
	@echo "compressing html..."
	@mkdir -p $(MAIN)
	@htmlcompressor $(HTML_OPTS) -o $(MAIN)/index.html $(RES)/index.html
	@cat $(MAIN)/index.html | sed -e "s/app.js/app.js?`date +%s`/" | sed -e "s/style.css/style.css?`date +%s`/" > $(MAIN)/index.html

$(MAIN)/css/style.css: $(RES)/css/style.css
	@echo "compressing css..."
	@mkdir -p $(MAIN)/css
	@cleancss -o $(MAIN)/css/style.css $(RES)/css/style.css

$(MAIN)/fonts: $(RES)/fonts
	@echo "copying fonts..."
	@mkdir -p $(MAIN)/fonts
	@cp -R $(RES)/fonts/ $(MAIN)/fonts

$(MAIN)/img: $(RES)/img $(RES)/favicon.ico
	@echo "copying images..."
	@cp -R $(RES)/img/ $(MAIN)/img
	@cp $(RES)/favicon.ico $(MAIN)/

$(MAIN)/js/main/app.js: $(MAIN_SRC)
	@echo "compiling js..."
	@lein with-profile prod do clean, cljsbuild once min
	@mkdir -p $(MAIN)/js/main
	@cp $(RES)/js/main/app.js $(MAIN)/js/main/

main: main-html main-js main-css main-fonts main-img

install-main: main
	@echo "syncing with: $(MAIN_TARGET)"
	@s3cmd -P sync $(MAIN)/ $(MAIN_TARGET)

serve-main:
	@sleep 1 && open "http://localhost:8000/" &
	@cd $(MAIN) && python3 -m http.server

############# Workshop

$(WS)/index.html: $(RES)/workshop.html
	@echo "compressing html..."
	@mkdir -p $(WS)
	@htmlcompressor $(HTML_OPTS) -o $(WS)/index.html $(RES)/workshop.html
	@cat $(WS)/index.html | sed -e "s/app.js/app.js?`date +%s`/" | sed -e "s/workshop.css/workshop.css?`date +%s`/" > $(WS)/index.html

$(WS)/css/workshop.css: $(RES)/css/workshop.css
	@echo "compressing css..."
	@mkdir -p $(WS)/css
	@cleancss -o $(WS)/css/workshop.css $(RES)/css/workshop.css

$(WS)/fonts: $(RES)/fonts
	@echo "copying fonts..."
	@mkdir -p $(WS)/fonts
	@cp -R $(RES)/fonts/ $(WS)/fonts

$(WS)/img: $(RES)/img/workshop $(RES)/favicon.ico
	@echo "copying images..."
	@cp -R $(RES)/img/ $(WS)/img
	@rm -rf $(WS)/img/projects $(WS)/img/all-commits.svg
	@cp $(RES)/favicon.ico $(WS)/

$(WS)/js/workshop/app.js: $(WS_SRC)
	@echo "compiling js..."
	@lein with-profile prod do clean, cljsbuild once min-workshop
	@mkdir -p $(WS)/js/workshop
	@cp $(RES)/js/workshop/app.js $(WS)/js/workshop/

ws: ws-html ws-js ws-css ws-fonts ws-img

install-ws: ws
	@echo "syncing with: $(WS_TARGET)"
	@s3cmd -P sync $(WS)/ $(WS_TARGET)

serve-ws:
	@sleep 1 && open "http://localhost:8000/" &
	@cd $(WS) && python3 -m http.server

clean:
	@echo "cleaning..."
	@rm -rf $(BUILD)

.PHONY: clean serve-main serve-ws
