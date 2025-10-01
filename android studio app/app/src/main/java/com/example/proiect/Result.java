package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Result extends Fragment {

    private TextView textViewRoot1, textViewRoot2, textViewRoot3;
    private Button buttonGenerateGraph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        textViewRoot1 = view.findViewById(R.id.textViewRoot1);
        textViewRoot2 = view.findViewById(R.id.textViewRoot2);
        textViewRoot3 = view.findViewById(R.id.textViewRoot3);
        buttonGenerateGraph = view.findViewById(R.id.buttonGenerateGraph);

        double a = getArguments().getDouble("a");
        double b = getArguments().getDouble("b");
        double c = getArguments().getDouble("c");
        double d = getArguments().getDouble("d");

        String[] roots = solveEquation(a, b, c, d);

        buttonGenerateGraph.setVisibility(View.GONE);

        if (a != 0) {
            if (roots.length == 0) {
                textViewRoot1.setText("Nicio radacina reala.");
                textViewRoot2.setText("");
                textViewRoot3.setText("");
            } else if (roots.length == 1) {
                textViewRoot1.setText("O radacina reala: " + roots[0]);
                textViewRoot2.setText("");
                textViewRoot3.setText("");
                buttonGenerateGraph.setVisibility(View.VISIBLE);
            } else if (roots[0].contains("i") || (roots.length > 1 && roots[1].contains("i")) || (roots.length > 2 && roots[2].contains("i"))) {
                textViewRoot1.setText("Radacina 1: " + roots[0]);
                if (roots.length > 1) textViewRoot2.setText("Radacina 2: " + roots[1]);
                if (roots.length > 2) textViewRoot3.setText("Radacina 3: " + roots[2]);
            } else {
                textViewRoot1.setText("Radacina 1: " + roots[0]);
                if (roots.length > 1) textViewRoot2.setText("Radacina 2: " + roots[1]);
                if (roots.length > 2) textViewRoot3.setText("Radacina 3: " + roots[2]);
                buttonGenerateGraph.setVisibility(View.VISIBLE);
            }
        } else if (b != 0) {
            roots = solveQuadraticEquation(b, c, d);
            if (roots.length == 0) {
                textViewRoot1.setText("Nicio radacina reala.");
                textViewRoot2.setText("");
                textViewRoot3.setText("");
            } else if (roots.length == 1) {
                textViewRoot1.setText("O radacina reala: " + roots[0]);
                textViewRoot2.setText("");
                textViewRoot3.setText("");
                buttonGenerateGraph.setVisibility(View.VISIBLE);
            } else if (roots[0].contains("i") || roots[1].contains("i")) {
                textViewRoot1.setText("Radacina 1: " + roots[0]);
                textViewRoot2.setText("Radacina 2: " + roots[1]);
                textViewRoot3.setText("");
            } else {
                textViewRoot1.setText("Radacina 1: " + roots[0]);
                textViewRoot2.setText("Radacina 2: " + roots[1]);
                textViewRoot3.setText("");
                buttonGenerateGraph.setVisibility(View.VISIBLE);
            }
        } else if (c != 0) {
            roots = solveLinearEquation(c, d);
            textViewRoot1.setText("O radacina reala: " + roots[0]);
            textViewRoot2.setText("");
            textViewRoot3.setText("");
            buttonGenerateGraph.setVisibility(View.VISIBLE);
        } else if (d != 0) {
            textViewRoot1.setText("Nicio solutie.");
            textViewRoot2.setText("");
            textViewRoot3.setText("");
        } else {
            textViewRoot1.setText("O infinitate de solutii.");
            textViewRoot2.setText("");
            textViewRoot3.setText("");
        }

        buttonGenerateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoadingActivity.class);

                intent.putExtra("a", a);
                intent.putExtra("b", b);
                intent.putExtra("c", c);
                intent.putExtra("d", d);
                intent.putExtra("nextActivity", "GraphActivity");

                startActivity(intent);
            }
        });

        return view;
    }

    public static String[] solveEquation(double a, double b, double c, double d) {
        if (a == 0) {
            return solveQuadraticEquation(b, c, d);
        } else {
            // Solve cubic equation ax^3 + bx^2 + cx + d = 0
            // Using Cardano's method
            double f = ((3*c/a) - ((b*b)/(a*a)))/3;
            double g = ((2*(b*b*b)/(a*a*a)) - (9*b*c)/(a*a) + (27*d/a))/27;
            double h = ((g*g)/4) + ((f*f*f)/27);

            if(h > 0) {
                // One real root and two complex roots
                double r = -(g/2) + Math.sqrt(h);
                double s = Math.cbrt(r);
                double t = -(g/2) - Math.sqrt(h);
                double u = Math.cbrt(t);

                double root1 = (s + u) - (b/(3*a));
                return new String[]{String.format("%.2f", root1)};
            } else if(f == 0 && g == 0 && h == 0) {
                // All 3 roots are real and equal
                double root = -Math.cbrt(d/a);
                return new String[]{String.format("%.2f", root)};
            } else if(h <= 0) {
                // All 3 roots are real
                double i = Math.sqrt(((g*g)/4) - h);
                double j = Math.cbrt(i);
                double k = Math.acos(-(g/(2*i)));
                double l = -j;
                double m = Math.cos(k/3);
                double n = Math.sqrt(3) * Math.sin(k/3);
                double p = -(b/(3*a));

                double root1 = 2*j*Math.cos(k/3) - (b/(3*a));
                double root2 = l * (m + n) + p;
                double root3 = l * (m - n) + p;

                return new String[]{
                        String.format("%.2f", root1),
                        String.format("%.2f", root2),
                        String.format("%.2f", root3)
                };
            } else {
                return new String[]{"Nicio solutie."};
            }
        }
    }

    public static String[] solveQuadraticEquation(double a, double b, double c) {
        if (a == 0) {
            return solveLinearEquation(b, c);
        } else {
            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) {
                double realPart = -b / (2 * a);
                double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
                String root1 = String.format("%.2f + %.2fi", realPart, imaginaryPart);
                String root2 = String.format("%.2f - %.2fi", realPart, imaginaryPart);
                return new String[]{root1, root2};
            } else if (discriminant == 0) {
                double root = -b / (2 * a);
                return new String[]{String.format("%.2f", root)};
            } else {
                double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                return new String[]{String.format("%.2f", root1), String.format("%.2f", root2)};
            }
        }
    }

    public static String[] solveLinearEquation(double a, double b) {
        if (a == 0) {
            if (b == 0) {
                return new String[]{"O infinitate de solutii."}; // Infinite solutions
            } else {
                return new String[]{"Nicio solutie."}; // No solution
            }
        } else {
            double root = -b / a;
            return new String[]{String.format("%.2f", root)};
        }
    }
}
