import re

with open("app/src/main/java/com/example/ui/screens/WelcomeScreen.kt", "r") as f:
    content = f.read()

# Pattern for Current Visit Hero Card
current_visit_pattern = r"(            // Current Visit Hero Card.*?)(            // Two Large Side-by-Side Primary Action Cards)"
current_visit_match = re.search(current_visit_pattern, content, re.DOTALL)
if not current_visit_match:
    print("Could not find Current Visit Hero Card")
    exit(1)

current_visit_str = current_visit_match.group(1)

# Pattern for Primary Action Cards + Quick Shortcuts
actions_pattern = r"(            // Two Large Side-by-Side Primary Action Cards.*?)(            // Recent Activity Section)"
actions_match = re.search(actions_pattern, content, re.DOTALL)
if not actions_match:
    print("Could not find Action Cards")
    exit(1)

actions_str = actions_match.group(1)

# Combine them in reversed order
combined_new = actions_str + current_visit_str

# Replace in content
full_old = current_visit_str + actions_str
content = content.replace(full_old, combined_new)

with open("app/src/main/java/com/example/ui/screens/WelcomeScreen.kt", "w") as f:
    f.write(content)
print("Done")
