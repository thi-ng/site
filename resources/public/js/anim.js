(function() {
  var id = 0;
  var numItems = 8;
  var items = document.querySelectorAll("#testimonials div");

  var clsFadein = " fadeIn";
  var clsFadeout = " fadeOut";
  var clsHidden = " hidden";

  var delayShow = 2500;
  var delayFade = 1050;
  
  function fadeOut() {
    items[id].className = items[id].className.replace(clsFadein, clsFadeout);
    setTimeout(fadeInNext, delayFade);
  }

  function fadeInNext() {
    items[id].className = items[id].className.replace(clsFadeout, clsHidden);
    id = (id + 1) % numItems;
    items[id].className = items[id].className.replace(clsHidden, "");
    items[id].className += clsFadein;
    setTimeout(fadeOut, delayShow);
  }

  setTimeout(fadeOut, delayShow);
})();
