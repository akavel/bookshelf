(require 'android-defs)

(define-alias Override java.lang.Override)

(define-alias ViewGroup android.view.ViewGroup)
(define-alias LayoutParams android.view.ViewGroup$LayoutParams)
(define-alias MarginLayoutParams android.view.ViewGroup$MarginLayoutParams)
(define-alias LinearLayoutParams android.widget.LinearLayout$LayoutParams)
(define-alias ListView android.widget.ListView)
(define-alias ViewPager android.support.v4.view.ViewPager)
(define-alias Typeface android.graphics.Typeface)
(define-alias Gravity android.view.Gravity)
(define-alias Log android.util.Log)

(define-constant ID_CLIPBOARD ::int 101)
(define-constant ID_SHELVES ::int 102)
(define-constant ID_TITLE ::int 103)
(define-constant ID_AUTHOR ::int 104)
(define-constant ID_PATH ::int 105)

(define-simple-class Book ()
  (title ::string)
  (author ::string)
  (path ::string))
  
(define-simple-class BookView (LinearLayout)
  (book ::Book access: 'private)
  (title-view ::TextView)
  (author-view ::TextView)
  (path-view ::TextView)
  ((*init* (book_ ::Book) (context ::android.content.Context) (attrs ::android.util.AttributeSet))
    (invoke-special LinearLayout (this) '*init* context attrs)
    (set! book book_)
    (orientation (this) LinearLayout:VERTICAL)
    (layout-params (this) (LayoutParams LayoutParams:FILL_PARENT LayoutParams:FILL_PARENT))
    
    (Log:i "MC" "new BookView()")
    
    (addView (TextView
      id: ID_TITLE
      layout-params: (LayoutParams LayoutParams:WRAP_CONTENT LayoutParams:WRAP_CONTENT
        ; TODO: android:layout_marginLeft="@dimen/activity_horizontal_margin"
        )
      text: "Title"
      ; TODO: android:textAppearance="?android:attr/textAppearanceLarge"
      typeface: (Typeface:default-from-style Typeface:BOLD)))
    (addView (TextView
      id: ID_AUTHOR
      layout-params: (LayoutParams LayoutParams:WRAP_CONTENT LayoutParams:WRAP_CONTENT
        ; TODO: android:layout_marginLeft="@dimen/activity_horizontal_margin"
        )
      text: "Author"
      ; TODO: android:textAppearance="?android:attr/textAppearanceMedium"
      ))
    (addView (TextView
      id: ID_PATH
      layout-params: (LinearLayoutParams LayoutParams:WRAP_CONTENT LayoutParams:WRAP_CONTENT
        gravity: Gravity:RIGHT
        ; TODO: android:layout_marginLeft="@dimen/activity_horizontal_margin"
        )
      text: "/file/path"
      ; TODO: android:textAppearance="?android:attr/textAppearanceSmall"
      ))
    )
  ((update) ::void
    (define book (this):book)
    (when (not (eq? #!null book))
      (TextView:set-text ((this):title-view) (as string (book:title)))
      (TextView:set-text ((this):author-view) (as string (book:author)))
      (TextView:set-text ((this):path-view) (as string (book:path)))))
)

;TODO: ArrayAdapter requires a view resource ID in constructor, so it seems I must move BookView layout to XML
;(define-simple-class ClipboardAdapter (android.widget.ArrayAdapter[Book])
;  (*init* (context ::Context) (
                      

;(activity helloworld
(define-simple-class helloworld (android.app.Activity)
;  (on-create-view
  ((on-create (sis ::android.os.Bundle)) ::void
    (invoke-special android.app.Activity (this) 'onCreate sis)
    (define clipboard-adapter (object (android.widget.BaseAdapter)
      (books init: [(Book title: "Pan Tadeusz" author: "Adam Mickiewicz" path: "/mnt/sdcard/foo/bar/baz.epub")])
      ((*init*)
        (Log:i "MC" "new clipboard-adapter()"))
      ((getCount @Override) ::int
        (Log:i "MC" "get-count()")
        (vector-length ((this):books)))
      ((get-item @Override (i ::int)) ::Object
        (if (>= i ((this):get-count))
          #!null
          (vector-ref ((this):books) i)))
      ((get-item-id @Override (i ::int)) ::long
        (if (>= i ((this):get-count))
          -1
          i))
      ((get-item-view-type @Override (i ::int)) ::int 0)
      ((get-view @Override (i ::int) (convertView ::View) (parent ::ViewGroup)) ::View
        (if (>= i ((this):get-count))
          #!null
        (let ((book (vector-ref ((this):books) i))
              (view (as BookView convertView)))
        (if (not (eq? #!null view))
          view
        (BookView book (parent:get-context) #!null)))))
      ((get-view-type-count @Override) ::int 1)
      ((has-stable-ids @Override) ::boolean #t)
      ((is-empty @Override) ::boolean
        (= ((this):get-count) 0))
      ((are-all-items-enabled @Override) ::boolean #t)
      ((is-enabled @Override (i ::int)) ::boolean #t)))
    (Log:i "MC" "helloworld:on-create-view()")

;    ((this):set-content-view
;      (LinearLayout (this)
;        orientation: LinearLayout:VERTICAL
;        layout-params: (LayoutParams LayoutParams:FILL_PARENT LayoutParams:FILL_PARENT)
;        (TextView (this)
;          text: "Foo"
;          layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 99)
;          )
;        (TextView (this)
;          text: "Bar"
;          layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 0))
;      )))
    
    (setContentView
      (LinearLayout (this)
        orientation: LinearLayout:VERTICAL
        layout-params: (LayoutParams LayoutParams:FILL_PARENT LayoutParams:FILL_PARENT)
        (ListView (this)
          id: ID_CLIPBOARD
          layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 99)
          ;adapter: clipboard-adapter
          )
        (ViewPager (this)
          id: ID_SHELVES
          layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 0))
      ))
    ((as ListView (findViewById ID_CLIPBOARD)):setAdapter clipboard-adapter)
    (clipboard-adapter:notifyDataSetChanged)
    )
  ;(on-create-options-menu (menu ::android.view.Menu)
  ;  ; Inflate the menu; this adds items to the action bar if it is present.
  ;  ((get-menu-inflater):inflate
) 
  
;    (define counter ::integer 0)
;    (define counter-view
;      (TextView text: "Not clicked yet."))
;    (LinearLayout orientation: LinearLayout:VERTICAL
;      (TextView text: "Hello Android from Kawa Scheme!")
;      (Button
;        text: "Click here!"
;        on-click-listener: (lambda (e)
;                              (set! counter (+ counter 1))
;                              (counter-view:setText
;                                (format "Clicked ~d times." counter))))
;      counter-view)))
  
;   (android.widget.TextView (this)
;    text: "Hello, Android from Kawa Scheme!")))