import os
import re

screens_dir = r'c:\Users\willi\AndroidStudioProjects\SkilldConect-main\app\src\main\java\com\skillconnect\app\ui\screens'

for filename in os.listdir(screens_dir):
    if not filename.endswith('.kt'):
        continue
    filepath = os.path.join(screens_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # We want to replace 'color = NeumorphicColors.primary' inside Buttons to 'color = Color.White'
    # Or for the Cancel button we want to add gradientBrush = null
    
    # Fix 1: Make sure any text with NeumorphicColors.primary inside a blue button is white.
    # Actually it's safer to just replace specific known ones.
    
    # In MentorScreen.kt, they explicitly passed backgroundColor = Color.White but forgot gradientBrush = null
    if 'MentorScreen.kt' in filename:
        content = content.replace(
            'backgroundColor = Color.White\n            ) {',
            'backgroundColor = Color.White,\n                gradientBrush = null\n            ) {'
        )
    
    # In SettingsScreens.kt
    if 'SettingsScreens.kt' in filename:
        # Agregar habilidad button is completely blue, so its text should be white
        content = content.replace(
            'Text("Agregar habilidad", color = NeumorphicColors.primary',
            'Text("Agregar habilidad", color = Color.White'
        )
        
        # Cancelar button has color = NeumorphicColors.muted but no gradientBrush = null
        content = content.replace(
            'onClick = {\n                                    showAddDialog = false\n                                    newSkillName = ""\n                                },\n                                modifier = Modifier.weight(1f)\n                            ) {',
            'onClick = {\n                                    showAddDialog = false\n                                    newSkillName = ""\n                                },\n                                modifier = Modifier.weight(1f),\n                                backgroundColor = NeumorphicColors.surface,\n                                gradientBrush = null\n                            ) {'
        )

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print('Styles fixed.')
