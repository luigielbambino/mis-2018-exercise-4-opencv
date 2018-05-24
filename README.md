# Excercise 4 - OpenCV

This is an app created by Luis Alberto Alvarez Zendejas (119446) and Muhammad Qumail (119432) for an assignment in Mobile Information Systems taught in the summer semester of 2018 at Bauhaus University Weimar, Germany

Drawing the "Red Nose"
Once we have identified a face and eyes, the app calculates position and size of the Red Nose according to the standard proportions of human faces.

Horizontal position is calculated by getting the center of the face dividing the face width by 2
nose position = face position in x + (face width / 2);

The vertical position is determined by finding the middle of the face, where the eyes should be located, and add the size of the eye divided by two
nose position = face position in y + (face height / 2) + (eyes height / 2);

The Nose size is calculated by dividing the face width by 7. 
nose size = face width / 7;