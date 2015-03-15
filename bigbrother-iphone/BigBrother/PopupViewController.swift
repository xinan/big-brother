//
//  PopupViewController.swift
//  BigBrother
//
//  Created by Liu Xinan on 15/3/15.
//  Copyright (c) 2015 Liu Xinan. All rights reserved.
//

import UIKit

class PopupViewController: UIViewController {
    
    @IBOutlet weak var rating: UILabel!
    @IBOutlet weak var amount: UILabel!
    
    var image: UIImage? = nil
    var stars: String = ""
    var left: String = ""
    @IBOutlet weak var imageView: UIImageView!
    @IBAction func reject(sender: UIBarButtonItem) {
        var rejectAlert = UIAlertController(title: "Reject Offer", message: "Are you sure?", preferredStyle: UIAlertControllerStyle.Alert)
        
        rejectAlert.addAction(UIAlertAction(title: "Cancel", style: .Default, handler: { (action: UIAlertAction!) in
        }))
        rejectAlert.addAction(UIAlertAction(title: "Sure", style: .Default, handler: { (action: UIAlertAction!) in
            self.dismissViewControllerAnimated(true, completion: nil)
        }))
        
        presentViewController(rejectAlert, animated: true, completion: nil)
    }
    @IBAction func accept(sender: UIBarButtonItem) {
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.imageView.image = image?
        self.rating.text = stars;
        self.amount.text = left;
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let vvc = segue.destinationViewController as? VoucherViewController {
            if let identifier = segue.identifier {
                if identifier == "showCode" {

                }
            }
        }
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
