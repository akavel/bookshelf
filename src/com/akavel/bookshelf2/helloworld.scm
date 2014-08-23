(require 'android-defs)

(define-alias LayoutParams android.view.ViewGroup$LayoutParams)
(define-alias MarginLayoutParams android.view.ViewGroup$MarginLayoutParams)
(define-alias LinearLayoutParams android.widget.LinearLayout$LayoutParams)
(define-alias ListView android.widget.ListView)
(define-alias ViewPager android.support.v4.view.ViewPager)
(define-alias Typeface android.graphics.Typeface)
(define-alias Gravity android.view.Gravity)

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
    
    ((this):add-view (TextView
      id: ID_TITLE
      layout-params: (LayoutParams LayoutParams:WRAP_CONTENT LayoutParams:WRAP_CONTENT
        ; TODO: android:layout_marginLeft="@dimen/activity_horizontal_margin"
        )
      text: "Title"
      ; TODO: android:textAppearance="?android:attr/textAppearanceLarge"
      typeface: (Typeface:default-from-style Typeface:BOLD)))
    ((this):add-view (TextView
      id: ID_AUTHOR
      layout-params: (LayoutParams LayoutParams:WRAP_CONTENT LayoutParams:WRAP_CONTENT
        ; TODO: android:layout_marginLeft="@dimen/activity_horizontal_margin"
        )
      text: "Author"
      ; TODO: android:textAppearance="?android:attr/textAppearanceMedium"
      ))
    ((this):add-view (TextView
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
                      

(activity helloworld
  (on-create-view
    (LinearLayout
      orientation: LinearLayout:VERTICAL
      layout-params: (LayoutParams LayoutParams:FILL_PARENT LayoutParams:FILL_PARENT)
      (ListView
        id: ID_CLIPBOARD
        layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 99))
      (ViewPager
        id: ID_SHELVES
        layout-params: (LinearLayoutParams LayoutParams:FILL_PARENT 0 0))
    ))
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