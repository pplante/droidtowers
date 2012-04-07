using System;
using System.Collections.Generic;
using System.Windows.Forms;
using Microsoft.Win32;

namespace HappyDroidsWinHandlerRegister
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Registry.SetValue(@"HKEY_CLASSES_ROOT\droidtowers", null, "URL:droidtowers Protocol");
            Registry.SetValue(@"HKEY_CLASSES_ROOT\droidtowers", "URL Protocol", "");
            Registry.SetValue(@"HKEY_CLASSES_ROOT\droidtowers\DefaultIcon", null, "Sparky.exe,1");
            Registry.SetValue(@"HKEY_CLASSES_ROOT\droidtowers\shell\open\command", null, "\"C:\\Users\\Phil Plante\\Desktop\\Sparky.exe\" \"%1\"");

            MessageBox.Show("Done!");
        }
    }
}
