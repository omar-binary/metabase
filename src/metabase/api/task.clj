(ns metabase.api.task
  "/api/task endpoints"
  (:require [compojure.core :refer [GET]]
            [metabase.api.common :as api]
            [metabase.models.task-history :as task-history :refer [TaskHistory]]
            [metabase.task :as task]
            [metabase.util.schema :as su]
            [schema.core :as s]
            [toucan.db :as db]))


(api/defendpoint GET "/"
  "Fetch a list of recent tasks stored as Task History"
  [limit offset]
  {limit  (s/maybe su/IntStringGreaterThanZero)
   offset (s/maybe su/IntStringGreaterThanOrEqualToZero)}
  (api/check-superuser)
  (api/check-valid-page-params limit offset)
  (let [limit-int  (some-> limit Integer/parseInt)
        offset-int (some-> offset Integer/parseInt)]
    {:total  (db/count TaskHistory)
     :limit  limit-int
     :offset offset-int
     :data   (task-history/all limit-int offset-int)}))

(api/defendpoint GET "/:id"
  "Get `TaskHistory` entry with ID."
  [id]
  (api/read-check TaskHistory id))

(api/defendpoint GET "/info"
  "Return raw data about all scheduled tasks (i.e., Quartz Jobs and Triggers)."
  []
  (api/check-superuser)
  (task/scheduler-info))


(api/define-routes)
