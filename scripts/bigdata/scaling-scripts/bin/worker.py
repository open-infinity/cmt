
#
# Copyright (c) 2011 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# worker.py
# 
# @author Timo Saarinen
# @author Tommi Siitonen
# @version 1.0.0
# @since 1.0.0
#

from __future__ import division       # Python 3 forward compatibility
from __future__ import print_function # Python 3 forward compatibility

import time
import threading

# -----------------------------------------------------------------------------
# Executes one task per time
class Worker(threading.Thread):
    # Task 
    def __init__(self, manager):
        self.__manager = manager
        self.__stop_requested = False
        threading.Thread.__init__(self)
        
    def run(self):
        while self.__stop_requested == False:
            task = self.__manager.pop_task()
            if (task != None):
                try:
                    task.execute()
                finally:
                    self.__manager.complete_task(task)
            else:
                time.sleep(0.5) # TODO: notification-based approach would be more efficient

    def request_stop(self):
        self.__stop_requested = True

# Allocates given number of workers and coordinates task executions
class WorkerManager(object):
    def __init__(self, worker_count = 10):
        self.__lock = threading.Lock()
        self.__tasks = []
        self.__workers = []
        self.__incompleted_tasks_count = 0
        for i in xrange(0, worker_count):
            w = Worker(self)
            self.__workers.append(w)
            w.start()

    # Add task to be executed        
    def add_task(self, task):
        self.__lock.acquire()
        self.__tasks.append(task)
        self.__incompleted_tasks_count += 1;
        self.__lock.release()

    # Returns a new task
    def pop_task(self):
        self.__lock.acquire()
        n = len(self.__tasks)
        if n > 0:
            task = self.__tasks.pop(0)
            self.__lock.release()
            return task
        else:
            self.__lock.release()
            return None

    # Called when a task is ready. This is called even if the task failed
    def complete_task(self, task):
        self.__lock.acquire()
        self.__incompleted_tasks_count -= 1;
        self.__lock.release()

    # Blocks until all tasks are finished
    def wait_for_completion(self):
        # Loop until the task list is empty
        while True:
            self.__lock.acquire()
            n = self.__incompleted_tasks_count
            self.__lock.release()
            if n == 0:
                return
            time.sleep(1.0) # TODO: notification-based approach would be more efficient

    # Request all workers to stop
    def stop_workers(self):
        self.__lock.acquire()
        for worker in self.__workers:
            worker.request_stop()
        self.__lock.release()


