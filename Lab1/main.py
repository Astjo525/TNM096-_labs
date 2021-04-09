import numpy as np
import heapq as hq

import timeit

class State:

    def __init__(self, mat : np.array):
        self.state_mat = mat
        self.string_mat = ''
        self.h1 = 0
        self.h2 = 0
        self.cost = 0
        self.evaluation = 0

    def update_heuristics(self, val: int):
        
        #self.calc_h1()
        self.calc_h2()
        self.cost = val
        self.evaluation = self.cost + self.h1 + self.h2

    def calc_h1(self):

        goal = np.arange(1,10)
        goal[8] = -1
        goal = goal.reshape(3,3)

        a = (self.state_mat == goal) * 1
        a[2, 2] = 1

        self.h1 = (a == 0).sum()

    def calc_h2(self):
        goal = np.arange(1,10)
        goal[8] = 0
        init = np.ndarray.flatten(self.state_mat)

        for i, item in enumerate(init):
            prev_row, prev_col = int(i/3), i % 3
            goal_row, goal_col = int((item -1)/3), (item - 1) % 3
            self.h2 += abs(prev_row - goal_row) + abs(prev_col - goal_col)

    def mat_to_string(self):
        self.string_mat = self.state_mat.tobytes()


class PuzzleSolver:
    def __init__(self, start_state, goal_state):
        self.current_state = start_state
        self.goal_state = goal_state
        self.counter = 0
        self.open = []
        self.close = {}

    def heuristic_search(self):

        self.current_state.update_heuristics(0)
        self.current_state.mat_to_string()
        hq.heappush(self.open, (self.current_state.evaluation, self.counter, self.current_state))

        self.goal_state.mat_to_string()

        while(self.open):
            _, _ , self.current_state = hq.heappop(self.open)
            self.close[self.current_state.string_mat] = 1

            if(self.current_state.string_mat == self.goal_state.string_mat):
                print(self.current_state.state_mat)
                print(self.current_state.cost)
                return("Goal has been reached", self.current_state.state_mat)

            self.find_possible_movements()

        
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


    def handle_movement(self, old_pos, new_pos):

        self.counter += 1

        swaped_mat = self.swap(old_pos, new_pos)

        new_state = State(swaped_mat)
        new_state.mat_to_string()
        if(not new_state.string_mat in self.close):
            new_state.update_heuristics(self.current_state.cost + 1)
            hq.heappush(self.open, (new_state.evaluation, self.counter, new_state))

    
    def swap(self, curr_pos, adv_pos):
        adv_state = self.current_state.state_mat.copy()

        adv_state[curr_pos[0], curr_pos[1]], adv_state[adv_pos[0], adv_pos[1]] = adv_state[adv_pos[0], adv_pos[1]], adv_state[curr_pos[0], curr_pos[1]]

        return adv_state


if __name__ == '__main__':
    # NOTE: put goal state (and start state) in separate class?

    # Define goal matrix
    goal_mat = np.arange(1,10)
    goal_mat[8] = 0
    goal_mat = goal_mat.reshape(3,3)

    goal = State(goal_mat)

    # Random start matrix
    # start_mat = goal_mat.copy()
    # np.random.shuffle(start_mat)
    # start_mat = start_mat.reshape(3,3)

    # EASY
    #start_mat = np.array([[4, 1, 3], [7, 2, 6], [0, 5, 8]])

    # MEDIUM
    #start_mat = np.array([[7, 2, 4], [5, 0, 6], [8, 3, 1]])

    # DIFFICULT
    #start_mat = np.array([[6, 4, 7], [8, 5, 0], [3, 2, 1]])

    # VERY DIFFICULT
    start_mat = np.array([[8, 6, 7], [2, 5, 4], [3, 0, 1]])

    init = State(start_mat)
    
    solution = PuzzleSolver(init, goal)

    start_time = timeit.default_timer()
    solution.heuristic_search()
    elapsed_time = timeit.default_timer() - start_time
    print("Time elapsed: ", elapsed_time)


