import numpy as np
import heapq as hq

import timeit

class State:

    def __init__(self, mat : np.array):
        self.__state_mat = mat
        self.__h1 = self.calc_h1()
        self.__cost = 0 # Every movement gets cost function = 1? Increasing 1 for each movement. Can also save path.
        self.__evaluation = self.__cost + self.__h1
        
    def calc_h1(self):
        # TODO: where to place goal?
        goal = np.arange(1,10)
        goal[8] = -1
        goal = goal.reshape(3,3)

        a = (self.__state_mat == goal) * 1
        a[2, 2] = 1

        return (a == 0).sum()

    def get_h1(self):
        return self.__h1
            
    def get_cost(self):
        return self.__cost

    def get_evaluation(self):
        return self.__evaluation

    def get_state_mat(self):
        return self.__state_mat

    def set_cost(self, val : int):
        # TODO_ fix
        self.__cost = val
        self.__evaluation = self.__cost + self.__h1

    # Overide < operator
    def __lt__(self, rhs):
        return self.__evaluation < rhs.__evaluation

    # Override == operator
    def __eq__(self, rhs):
        return np.array_equal(self.__state_mat, rhs.__state_mat)

 



class PuzzleSolver:
    def __init__(self, start_state, goal_state):
        self.current_state = start_state
        self.goal_state = goal_state
        self.open = [self.current_state]
        hq.heapify(self.open)
        self.close = []

    def heuristic_search(self):
        # NOTE: Do we need to know what path we took? 

        moves = 0

        while(self.open):
            self.current_state = hq.heappop(self.open)
            hq.heappush(self.close, (self.current_state))

            if(self.current_state == self.goal_state):
                return("Goal has been reached", self.current_state.get_state_mat())

            moves += 1
            self.find_possible_movements()

    def swap(self, curr_pos, adv_pos):
        adv_state = self.current_state.get_state_mat().copy()
        # temp = advanced_state[advanced_pos[0], advanced_pos[1]]
        # advanced_state[current_pos[0], current_pos[1]] = temp
        # advanced_state[advanced_pos[0], advanced_pos[1]] = 0

        adv_state[curr_pos[0], curr_pos[1]], adv_state[adv_pos[0], adv_pos[1]] = adv_state[adv_pos[0], adv_pos[1]], adv_state[curr_pos[0], curr_pos[1]]

        return adv_state
    
    def duplicate_state(self, new_state):

        if new_state in self.close:
            return True

        return False
        
    def find_possible_movements(self):
        posy, posx = np.where(self.current_state.get_state_mat() == 0)

        # Go up
        if(posy - 1 >= 0):
            advanced = self.swap([posy, posx], [posy -1, posx])
            new_state = State(advanced)
            if(not self.duplicate_state(new_state)):
                new_state.set_cost(self.current_state.get_cost() + 1)
                hq.heappush(self.open, new_state)
        # Go down
        if(posy + 1 <= 2):
            advanced = self.swap([posy, posx], [posy +1, posx])
            new_state = State(advanced)
            if(not self.duplicate_state(new_state)):
                new_state.set_cost(self.current_state.get_cost() + 1)
                hq.heappush(self.open, new_state)
        # Go left
        if(posx - 1 >= 0):
            advanced = self.swap([posy, posx], [posy, posx -1])
            new_state = State(advanced)
            if(not self.duplicate_state(new_state)):
                new_state.set_cost(self.current_state.get_cost() + 1)
                hq.heappush(self.open, new_state)
        # Go right
        if(posx + 1 <= 2):
            advanced = self.swap([posy, posx], [posy, posx +1])
            new_state = State(advanced)
            if(not self.duplicate_state(new_state)):
                new_state.set_cost(self.current_state.get_cost() + 1)
                hq.heappush(self.open, new_state)
        

if __name__ == '__main__':
    # NOTE: put goal state (and start state) in separate class?
    goal_state = np.arange(1,10)
    goal_state[8] = 0
    # start_state = goal_state.copy()
    # np.random.shuffle(start_state)
    # start_state = start_state.reshape(3,3)
    #start_state = np.array([[2, 5, -1], [1, 4, 8], [7, 3, 6]])

    # EASY
    #start_state = np.array([[4, 1, 3], [7, 2, 6], [0, 5, 8]])

    # MEDIUM
    start_state = np.array([[7, 2, 4], [5, 0, 6], [8, 3, 1]])

    # DIFFICULT
    #start_state = np.array([[6, 4, 7], [8, 5, 0], [3, 2, 1]])


    goal_state = goal_state.reshape(3,3)
    init = State(start_state)
    goal = State(goal_state)
    solution = PuzzleSolver(init, goal)

    start_time = timeit.default_timer()
    solution.heuristic_search()
    elapsed_time = timeit.default_timer() - start_time
    print("Time elapsed: ", elapsed_time)

