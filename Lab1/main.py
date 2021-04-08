import numpy as np
import heapq as hq

import timeit

class State:

    def __init__(self, mat : np.array):
        self.state_mat = mat
        self.string_mat = ''
        self.h1 = 0
        self.cost = 0 # Every movement gets cost function = 1? Increasing 1 for each movement. Can also save path.
        self.evaluation = 0
        
    # def calc_h1(self):
    #     # TODO: where to place goal?
    #     #posy, posx = np.where(self.current_state.state_mat == 0)
    #     goal = np.arange(1,10)
    #     goal[8] = -1
    #     goal = goal.reshape(3,3)

    #     a = (self.state_mat == goal) * 1
    #     a[2, 2] = 1

    #     return (a == 0).sum()

    # def update_cost_evaluation(self, val : int):
    #     self.cost = val
    #     self.evaluation = self.cost + self.h1

    def update_heuristics(self, val: int):
        goal = np.arange(1,10)
        goal[8] = -1
        goal = goal.reshape(3,3)

        a = (self.state_mat == goal) * 1
        a[2, 2] = 1

        self.h1 = (a == 0).sum()
        self.cost = val
        self.evaluation = self.cost + self.h1

    def mat_to_string(self):
        self.string_mat = self.state_mat.tobytes()


class PuzzleSolver:
    def __init__(self, start_state, goal_state):
        self.current_state = start_state
        self.goal_state = goal_state
        self.counter = 0
        self.open = []
        #self.open = [(self.current_state.evaluation, self.counter, self.current_state)]
        #hq.heapify(self.open)
        self.close = {}

    def heuristic_search(self):
        # NOTE: Do we need to know what path we took? 

        moves = 0

        self.current_state.update_heuristics(0) # ÄNDRAT: Blev bättre
        self.current_state.mat_to_string()
        hq.heappush(self.open, (self.current_state.evaluation, self.counter, self.current_state)) # ÄNDRAD: blev lite bättre

        self.goal_state.mat_to_string()

        while(self.open):
            _, _ , self.current_state = hq.heappop(self.open)
            #self.close.update({matrix_to_string(self.current_state.get_state_mat())})
            #hq.heappush(self.close, (self.current_state.string_mat))
            self.close[self.current_state.string_mat] = 1

            if(self.current_state.string_mat == self.goal_state.string_mat):
                print(self.current_state.state_mat)
                print(self.current_state.cost)
                return("Goal has been reached", self.current_state.state_mat)

            moves += 1
            self.find_possible_movements()

    def swap(self, curr_pos, adv_pos):
        adv_state = self.current_state.state_mat.copy()

        #adv_state[curr_pos[0], curr_pos[1]], adv_state[adv_pos[0], adv_pos[1]] = adv_state[adv_pos[0], adv_pos[1]], adv_state[curr_pos[0], curr_pos[1]]

        adv_state[curr_pos[0], curr_pos[1]] = adv_state[adv_pos[0], adv_pos[1]] # ÄNDRAT: blev typ lite snabbare
        adv_state[adv_pos[0], adv_pos[1]] = 0 

        return adv_state
    
    # def duplicate_state(self, new_state):

    #     if new_state in self.close:
    #         return True

    #     return False

    def handle_movement(self, old_pos, new_pos):

        self.counter += 1

        # swaped_mat = self.current_state.state_mat.copy()
        # swaped_mat[old_pos[0], old_pos[1]] = swaped_mat[new_pos[0], new_pos[1]]
        # swaped_mat[new_pos[0], new_pos[1]] = 0 

        swaped_mat = self.swap(old_pos, new_pos)

        new_state = State(swaped_mat)
        new_state.mat_to_string()
        if(not new_state.string_mat in self.close):
            new_state.update_heuristics(self.current_state.cost + 1)
            hq.heappush(self.open, (new_state.evaluation, self.counter, new_state))
        
        
    def find_possible_movements(self):
        posy, posx = np.where(self.current_state.state_mat == 0)

        # Go up
        if(posy - 1 >= 0):
            self.handle_movement([posy, posx], [posy -1, posx])
        # Go down
        if(posy + 1 <= 2):
            self.handle_movement([posy, posx], [posy +1, posx])
        # Go left
        if(posx - 1 >= 0):
            self.handle_movement([posy, posx], [posy, posx -1])
        # Go right
        if(posx + 1 <= 2):
            self.handle_movement([posy, posx], [posy, posx +1])


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
    #start_state = np.array([[7, 2, 4], [5, 0, 6], [8, 3, 1]])

    # DIFFICULT
    #start_state = np.array([[6, 4, 7], [8, 5, 0], [3, 2, 1]])

    # VERY DIFFICULT
    start_state = np.array([[8, 6, 7], [2, 5, 4], [3, 0, 1]])


    goal_state = goal_state.reshape(3,3)
    init = State(start_state)
    goal = State(goal_state)
    solution = PuzzleSolver(init, goal)

    start_time = timeit.default_timer()
    solution.heuristic_search()
    elapsed_time = timeit.default_timer() - start_time
    print("Time elapsed: ", elapsed_time)


