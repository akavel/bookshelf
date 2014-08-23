(require 'android-defs)
(activity helloworld
  (on-create-view
   (android.widget.TextView (this)
    text: "Hello, Android from Kawa Scheme!")))