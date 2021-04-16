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

        np.random.shuffle(self.variables)
        

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
            var3_ind = i+3

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


    def print(self):

        string_variables = self.variables.copy()

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


def min_conflicts(schedule, max_steps = 1000):
    current = schedule.variables

    for i in range(max_steps):
        current_conflicts = schedule.get_conflicts(current)

        if len(current_conflicts) == 0:
            return current

        var_ind = random.choice(current_conflicts)

        min_conflict = np.inf

        zero_positions = [i for i, e in enumerate(current) if e == 0]

        min_conflict = np.inf
        val = -1

        for i in range(len(zero_positions)):
            temp_assignment = current.copy()
            temp_assignment[zero_positions[i]] = temp_assignment[var_ind]
            temp_assignment[var_ind] = 0

            conflicts = schedule.get_conflicts(temp_assignment)
            current_conflict = len(conflicts)

            if(current_conflict < min_conflict):
                min_conflict = current_conflict
                val = zero_positions[i]

        current[val] = current[var_ind]
        current[var_ind] = 0

    return current


if __name__ == '__main__':
    schedule = Schedule()
    current = min_conflicts(schedule)

    schedule.print()
    