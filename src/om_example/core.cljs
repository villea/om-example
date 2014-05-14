(ns om-example.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:messages []}))

(defn add-message [app owner {:keys [sender message]}]
    (om/transact! app :messages #(conj % {:message message :sender sender}))
    (om/set-state! owner :sender "")
    (om/set-state! owner :message ""))

(defn add-message-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:message ""
       :sender ""})
    om/IRenderState
    (render-state [this state]  
      (dom/div nil
        (dom/input #js {:type "text" 
                        :placeholder "Sender" 
                        :value (:sender state)
                        :onChange #(om/set-state! owner :sender (.. % -target -value))
                        })
        (dom/input #js {:type "text" 
                        :placeholder "Message" 
                        :value (:message state)
                        :onChange #(om/set-state! owner :message (.. % -target -value))
                        })
        (when (and (> (count (:sender state)) 0)
                   (> (count (:message state)) 0))
            (dom/button #js {:onClick #(add-message app owner state)} "add"))))))

(defn messages-view [{:keys [messages]} owner]
    (if (> (count messages) 0)
    (apply dom/ul nil
        (for [msg messages]
            (dom/li nil (str (:sender msg) " : " (:message msg)))))
    (dom/h2 nil "no messages")))

(defn app-view [app owner]
    (dom/div nil
        (om/build add-message-view app)
        (om/build messages-view app)))

(om/root
  app-view
  app-state
  {:target (. js/document (getElementById "messages"))})
