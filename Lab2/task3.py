import numpy as np
import random

class Schedule:
    def __init__(self):

        course_list = [101, 102, 103, 104, 105, 106, 107,
                        201, 202, 203, 204, 205, 206,
                        301, 302, 303, 304,
                        401, 402, 403,
                        501, 502, 0, 0]

        self.variables = course_list
        

    def check_constraint(self, var1, var2):

        first_number = int(var1/100)
        second_number = int(var2 / 100)

        if(first_number == second_number and first_number != 5):
            return True

        return False


    def get_conflicts(self, assignment):

        conflict = []

        for i in range(0, 24, 3):
            var1_ind = i
            var2_ind = i+1
            var3_ind = i+2

            var1 = assignment[i]
            var2 = assignment[i+1]
            var3 = assignment[i+2]
            if(self.check_constraint(var1, var2)):
                conflict.append(var1_ind)
                conflict.append(var2_ind)

            if(self.check_constraint(var1, var3)):
                conflict.append(var3_ind)

                if var1 not in conflict:
                    conflict.append(var1_ind)

            if(self.check_constraint(var2, var3)):
                if var2 not in conflict:
                    conflict.append(var2_ind)

                if var3 not in conflict:
                    conflict.append(var3_ind)

        return conflict


    def print(self, variables):

        string_variables = variables.copy()

        for ind, val in enumerate(string_variables):
            if(val != 0):
                string_variables[ind] = "MT"+str(val)
            else:
                string_variables[ind] = '     '

        res = np.reshape(string_variables, (8,3))

        times = [9, 10, 11, 12, 1, 2, 3, 4]

        for i in range(len(res)):
            if(i == 0):
                print('{:<10s}{:>4s}{:>12s}{:>12s}'.format('', 'TP51', 'SP34', 'K3'))
                print('{:<10s}{:>4s}{:>12s}{:>12s}'.format('', '----', '----', '----'))
                print('{:<10d}{:>4s}{:>12s}{:>12s}'.format(times[i], res[i][0], res[i][1], res[i][2] ))
            else:
                print('{:<10d}{:>4s}{:>12s}{:>12s}'.format(times[i], res[i][0], res[i][1], res[i][2] ))


    def check_preferences(self, variables):
        var = variables.copy()
        variable_mat = np.reshape(var, (8, 3))

        no_preference = [0, 3, 7]
        preference = [4, 5]

        satisfied_preference = 0

        for i in no_preference:
            row = variable_mat[i]
            row = row[row == 0]
            satisfied_preference += len(row)

        for i in preference:
            row = variable_mat[i]
            row = np.floor(row/100).astype(int)
            row = row[row == 5]
            satisfied_preference += len(row)

        return satisfied_preference


def min_conflicts(schedule, max_steps = 1000):
    current = schedule.variables
    np.random.shuffle(current)

    #current = [202, 104, 103, 401, 301, 203, 402, 101, 0, 205, 303, 501, 106, 502, 201, 107, 204, 403, 105, 304, 206, 302, 0, 102]

    for i in range(max_steps):
        current_conflicts = schedule.get_conflicts(current)

        if len(current_conflicts) == 0:
            return current

        var_ind = random.choice(current_conflicts)

        min_conflict = np.inf

        random_val = current[var_ind]
        random_val_int = int(random_val/100)

        temp_assignment = current.copy()

        for j in range(0, 24, 3):
            row = temp_assignment[j:j+2]
            row_int = np.floor(np.array(row)/100).astype(int)
            if(len(row_int[row_int == random_val_int]) == 0):
                for ind, var in enumerate(row):
                    temp = temp_assignment.copy()
                    #var = row[i]

                    temp[j + ind], temp[var_ind] = temp[var_ind], temp[j + ind]
                    conflicts = schedule.get_conflicts(temp)
                    current_conflict = len(conflicts)
                    if(current_conflict < min_conflict):
                        min_conflict = current_conflict
                        current = temp

    return current
                        



            




    #     for i in range(len(zero_positions)):
    #         temp_assignment = current.copy()
    #         temp_assignment[zero_positions[i]] = temp_assignment[var_ind]
    #         temp_assignment[var_ind] = 0

    #         conflicts = schedule.get_conflicts(temp_assignment)
    #         current_conflict = len(conflicts)

    #         if(current_conflict < min_conflict):
    #             min_conflict = current_conflict
    #             val = zero_positions[i]

    #     current[val] = current[var_ind]
    #     current[var_ind] = 0

    # return current

def schedule_with_preference(schedule, max_iter = 300):

    max_preference = -1
    best_solution = []

    for i in range(max_iter):
        solution = min_conflicts(schedule)
        preference_count = schedule.check_preferences(solution)

        if(preference_count == 4):
            return solution

        if(preference_count > max_preference):
            max_preference = preference_count
            best_solution = solution


    return best_solution


if __name__ == '__main__':
    schedule = Schedule()
    #result = min_conflicts(schedule)

    result = schedule_with_preference(schedule)


    #test = [202, 104, 103, 401, 301, 203, 402, 101, 0, 205, 303, 501, 106, 502, 201, 107, 204, 403, 105, 304, 206, 302, 0, 102]



    schedule.print(result)
    