import numpy as np
import heapq

class State:
    # 3x3 matrix (current state)
    # h1 heuristic
    # h2 heuristic (Later)
    # cost function c(s) = 1
    # evaluation function f(s) = c(s) + h(s)
    def __init__(self, mat : np.array):
        self.state_mat = mat
        self.__h1 = self.calc_h1()
        self.__c = 1 # Every movement gets cost function = 1? Increasing 1 for each movement. Can also save path.
        self.__f = self.__c + self.__h1
        
    def calc_h1(self):
        counter = 1
        h1 = 0

        for i in self.state_mat:
            for j in i:
                counter += 1
                if(j != counter and counter != 9 and j != -1):
                    h1 += 1
        return h1

    def get_h1(self):
        return self.__h1
            
    def get_c(self):
        return self.__c

    def get_f(self):
        return self.__f

class PuzzleSolver:
    def __init__(self, start_state, goal_state):
        self.current_state = start_state
        self.goal_state = goal_state
        self.open = [] #TODO: better data structure
        self.close = []

    def heuristic_search(self):
        # NOTE: Do we need to know what path we took? 

        # Fill open with current state
        heapq.heappush(self.open, (self.current_state.get_f(), self.current_state))

        while(self.open):
            node = heapq.heappop(self.open)
            print(self.open)
            #heapq.heappush(self.close, node)
            self.find_possible_movements()

        
        # while open is not empty
            # Node = first node in open list (lowest evaluation function because sorted)
            # NodeList = open list without Node
            # Add Node to close list
            # If we have reached goal state, done
                # Return 

            # Add children to Node in open list (and sort)
                # Check if state exists in close

    def swap(self, current_pos, advanced_pos):
        advanced_state = self.current_state.state_mat.copy()
        temp = advanced_state[advanced_pos]
        advanced_state[current_pos] = temp
        advanced_state[advanced_pos] = -1

        return advanced_state


    def find_possible_movements(self):
        posy, posx = np.where(self.current_state.state_mat == -1)

        # Go up
        if(posy - 1 >= 0):
            advanced = self.swap([posy, posx], [posy -1, posx])
            new_state = State(advanced)
            heapq.heappush(self.open, (new_state.get_f(), new_state))
        # Go down
        if(posy + 1 <= 2):
            advanced = self.swap([posy, posx], [posy +1, posx])
            new_state = State(advanced)
            heapq.heappush(self.open, (new_state.get_f(), new_state))
        # Go left
        if(posx - 1 >= 0):
            advanced = self.swap([posy, posx], [posy, posx -1])
            new_state = State(advanced)
            heapq.heappush(self.open, (new_state.get_f(), new_state))
        # Go right
        if(posx + 1 <= 2):
            advanced = self.swap([posy, posx], [posy, posx +1])
            new_state = State(advanced)
            heapq.heappush(self.open, (new_state.get_f(), new_state))
        

            
        
if __name__ == '__main__':
    # NOTE: put goal state (and start state) in separate class?
    goal_state = np.arange(1,10)
    goal_state[8] = -1
    start_state = goal_state.copy()
    np.random.shuffle(start_state)
    start_state = start_state.reshape(3,3)
    goal_state = goal_state.reshape(3,3)
    init = State(start_state)
    goal = State(goal_state)
    solution = PuzzleSolver(init, goal)
    solution.heuristic_search()

