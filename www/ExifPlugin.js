window.exif = function(action, arr, callback, error) {
    cordova.exec(callback, error, "Exif", action, arr);
};
